resource "kubernetes_secret" "report_service" {
  metadata {
    name      = "report-service-secret"
    namespace = kubernetes_namespace.report_service.metadata[0].name
    
  }

  data = {
    # RDS Password
    SPRING_DATASOURCE_PASSWORD = var.rds_password

    # AWS Credentials (for S3 and SQS access from pod)
    AWS_ACCESS_KEY_ID     = var.aws_access_key_id
    AWS_SECRET_ACCESS_KEY = var.aws_secret_access_key
  }

  depends_on = [
    kubernetes_namespace.report_service
  ]
  
  type      = "Opaque"
}
