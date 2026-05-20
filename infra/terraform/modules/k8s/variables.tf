variable "environment" {
  description = "Environment name"
  type        = string
}

variable "container_image" {
  description = "Container image for report-service"
  type        = string
}

variable "app_name" {
  description = "Application name"
  type        = string
  default     = "report-service"
}

variable "app_port" {
  description = "Application port"
  type        = number
  default     = 8080
}

variable "aws_region" {
  description = "AWS region for environment variables"
  type        = string
  default     = "us-east-2"
}

variable "rds_jdbc_url" {
  description = "JDBC URL from the RDS module"
  type        = string
}

variable "rds_username" {
  description = "RDS username from the RDS module"
  type        = string
}

variable "rds_password" {
  description = "RDS password for the Kubernetes secret"
  type        = string
  sensitive   = true
}

variable "aws_access_key_id" {
  description = "AWS access key ID for the Kubernetes secret"
  type        = string
}

variable "aws_secret_access_key" {
  description = "AWS secret access key for the Kubernetes secret"
  type        = string
}

variable "aws_status_update_queue_url" {
  description = "SQS URL used by the application"
  type        = string
}
