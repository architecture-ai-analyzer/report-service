variable "rds_instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t3.micro"
}

variable "rds_allocated_storage" {
  description = "RDS allocated storage"
  type        = number
  default     = 20
}

variable "rds_password" {
  description = "RDS master password"
  type        = string
  sensitive   = true
}

variable "container_image" {
  description = "Container image for report-service"
  type        = string
  default     = "thiagofrederico/report-service:latest"
}

variable "aws_access_key_id" {
  description = "AWS Access Key ID for pod credentials"
  type        = string
  sensitive   = true
}

variable "aws_secret_access_key" {
  description = "AWS Secret Access Key for pod credentials"
  type        = string
  sensitive   = true
}

variable "service" {
  description = "Nome do serviço"
  type        = string
  default     = "report"
}

variable "health_check_path" {
  description = "Path do health check"
  type        = string
  default     = "/api/actuator/health"
}

variable "app_port" {
  description = "Porta da aplicação"
  type        = number
  default     = 8080
}

variable "app_name" {
  description = "Nome da aplicação"
  type        = string
  default     = "report-service"
}

variable "environment" {
  description = "Ambiente (dev, homolog, production)"
  type        = string
  default     = "dev"
}