output "rds_endpoint" {
  description = "RDS endpoint"
  value       = module.rds.endpoint
}

output "rds_address" {
  description = "RDS host address"
  value       = module.rds.address
}

output "rds_jdbc_url" {
  description = "JDBC URL for Spring Boot"
  value       = module.rds.jdbc_url
}

/*output "s3_bucket" {
  description = "S3 bucket name"
  value       = data.aws_s3_bucket.main.id
}*/

# SQS Queue URL - COMMENTED OUT: Queue will be created manually in AWS
# output "sqs_queue_url" {
#   description = "SQS queue URL"
#   value       = module.sqs.queue_url
# }

# IRSA - COMMENTED OUT: Using Kubernetes Secret instead
# output "irsa_role_arn" {
#   description = "IRSA role ARN"
#   value       = module.iam.role_arn
# }

# Kubernetes Outputs
output "k8s_namespace" {
  description = "Kubernetes namespace for report-service"
  value       = module.k8s.k8s_namespace
}

output "k8s_deployment_name" {
  description = "Kubernetes deployment name"
  value       = module.k8s.k8s_deployment_name
}

output "k8s_service_name" {
  description = "Kubernetes service name"
  value       = module.k8s.k8s_service_name
}

output "k8s_service_cluster_ip" {
  description = "Kubernetes service cluster IP"
  value       = module.k8s.k8s_service_cluster_ip
}

output "k8s_hpa_name" {
  description = "Kubernetes HPA name"
  value       = module.k8s.k8s_hpa_name
}

output "k8s_service_account_name" {
  description = "Kubernetes service account name"
  value       = module.k8s.k8s_service_account_name
}

output "loadbalancer_hostname" {
  description = "Hostname do Load Balancer"
  value       = module.k8s.loadbalancer_hostname
}