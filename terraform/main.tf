# Terraform AWS Provider
provider "aws" {
  region = "us-east-2"
}

# Parameter Store variables
resource "aws_ssm_parameter" "db_url" {
  name  = "/reto-accenture/db_url"
  type  = "String"
  value = "r2dbc:mysql://reto-accenture.czi4wuoi45uu.us-east-2.rds.amazonaws.com/prueba_accenture"
}
resource "aws_ssm_parameter" "db_username" {
  name  = "/reto-accenture/db_username"
  type  = "String"
  value = "admin"
}
resource "aws_ssm_parameter" "db_password" {
  name  = "/reto-accenture/db_password"
  type  = "SecureString"
  value = "Password"
}

# ECS Cluster
resource "aws_ecs_cluster" "api_cluster" {
  name = "reto-accenture-cluster"
}

# IAM Role for ECS Task Execution
resource "aws_iam_role" "ecs_task_execution_role" {
  name = "ecsTaskExecutionRole-reto-accenture"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_assume_role_policy.json
}

# Política específica para CloudWatch Logs
resource "aws_iam_policy" "cloudwatch_logs_policy" {
  name        = "cloudwatch-logs-policy-reto-nequi"
  description = "Permite a ECS escribir logs en CloudWatch"
  
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "logs:CreateLogStream",
          "logs:PutLogEvents",
          "logs:CreateLogGroup"
        ],
        Resource = "*"
      }
    ]
  })
}

# Adjuntar la política al rol de ejecución
resource "aws_iam_role_policy_attachment" "cloudwatch_logs_attachment" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = aws_iam_policy.cloudwatch_logs_policy.arn
}

data "aws_iam_policy_document" "ecs_task_assume_role_policy" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_policy" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}
resource "aws_iam_role_policy_attachment" "ssm_policy" {
  role       = aws_iam_role.ecs_task_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMReadOnlyAccess"
}

# ECS Task Definition
resource "aws_ecs_task_definition" "api_task" {
  family                   = "reto-accenture-api"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  container_definitions    = jsonencode([
    {
      name      = "reto-accenture-api"
      image     = "926413415764.dkr.ecr.us-east-2.amazonaws.com/accenture/reto-webflux:latest"
      portMappings = [{ containerPort = 7070, hostPort = 7070 }]
      environment = [
        { name = "DB_URL", valueFrom = aws_ssm_parameter.db_url.arn },
        { name = "DB_USERNAME", valueFrom = aws_ssm_parameter.db_username.arn },
        { name = "DB_PASSWORD", valueFrom = aws_ssm_parameter.db_password.arn }
      ]
      essential = true
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = "/ecs/reto-accenture-api"
          awslogs-region        = "us-east-2"
          awslogs-stream-prefix = "ecs"
        }
      }
    }
  ])
  depends_on = [aws_cloudwatch_log_group.ecs_logs]
}

# CloudWatch Log Group for ECS logs
resource "aws_cloudwatch_log_group" "ecs_logs" {
  name              = "/ecs/reto-accenture-api"
  retention_in_days = 14
}

#security group for ecs
resource "aws_security_group" "ecs_sg" {
  name        = "reto-accenture-ecs-sg"
  description = "Allow inbound traffic to ECS tasks"
  vpc_id      = var.vpc_id
  
  ingress {
    from_port   = 7070
    to_port     = 7070
    protocol    = "tcp"
    security_groups = [aws_security_group.lb_sg.id]
  }
  
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# ECS Service
resource "aws_ecs_service" "api_service" {
  name            = "reto-accenture-api-service"
  cluster         = aws_ecs_cluster.api_cluster.id
  task_definition = aws_ecs_task_definition.api_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"
  network_configuration {
    subnets          = var.subnets
    security_groups  = [aws_security_group.ecs_sg.id]
    assign_public_ip = true
  }
  load_balancer {
    target_group_arn = aws_lb_target_group.api_tg.arn
    container_name   = "reto-accenture-api"
    container_port   = 7070
  }
  depends_on = [aws_lb_listener.http]
}

# Security Group for Load Balancer
resource "aws_security_group" "lb_sg" {
  name        = "reto-accenture-lb-sg"
  description = "Allow HTTP inbound traffic"
  vpc_id      = var.vpc_id
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Application Load Balancer
resource "aws_lb" "api_alb" {
  name               = "reto-accenture-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.lb_sg.id]
  subnets            = var.subnets
}

resource "aws_lb_target_group" "api_tg" {
  name     = "reto-accenture-tg"
  port     = 7070
  protocol = "HTTP"
  vpc_id   = var.vpc_id
  target_type = "ip"
  health_check {
    enabled             = true
    healthy_threshold   = 2
    interval            = 30
    matcher             = "200-399"
    path                = "/valid/health"
    port                = "traffic-port"
    protocol            = "HTTP"
    timeout             = 15
  }
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.api_alb.arn
  port              = "80"
  protocol          = "HTTP"
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.api_tg.arn
  }
}

# API Gateway
resource "aws_apigatewayv2_api" "accenture_api" {
  name          = "accenture"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_stage" "accenture_stage" {
  api_id      = aws_apigatewayv2_api.accenture_api.id
  name        = "prod"
  auto_deploy = true
}

# Obtener solo subnets en zonas de disponibilidad compatibles
variable "compatible_subnets" {
  description = "Subnets en zonas de disponibilidad compatibles con API Gateway VPC Link"
  type        = list(string)
  default     = [
    "subnet-04e25c3f611dea201", 
    "subnet-011318b669fd47683", 
    "subnet-03c85514770353462"
  ]
}

resource "aws_apigatewayv2_integration" "alb_integration" {
  api_id                 = aws_apigatewayv2_api.accenture_api.id
  integration_type       = "HTTP_PROXY"
  integration_uri        = "http://${aws_lb.api_alb.dns_name}"
  integration_method     = "ANY"
  payload_format_version = "1.0"
  request_parameters = {
    "overwrite:path" = "$request.path"
  }
}

resource "aws_apigatewayv2_route" "alb_route" {
  api_id    = aws_apigatewayv2_api.accenture_api.id
  route_key = "ANY /{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.alb_integration.id}"
}

# Variables
variable "aws_region" {}
variable "vpc_id" {}
variable "subnets" { type = list(string) }
variable "db_url" {}
variable "db_username" {}
variable "db_password" {}
