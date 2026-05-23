resource "kubernetes_secret" "report_service" {
  metadata {
    name      = "${var.app_name}-secret"
    namespace = kubernetes_namespace.report_service.metadata[0].name
  }

  data = {
    SPRING_DATASOURCE_PASSWORD = var.rds_password
    AWS_ACCESS_KEY_ID          = var.aws_access_key_id
    AWS_SECRET_ACCESS_KEY      = var.aws_secret_access_key
  }

  type = "Opaque"

  depends_on = [
    kubernetes_namespace.report_service
  ]
}
