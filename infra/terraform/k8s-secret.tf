resource "kubernetes_secret" "report_service_secret" {
  metadata {
    name      = "report-service-db-secret"
    namespace = kubernetes_namespace.report_service.metadata[0].name
  }

  data = {
    POSTGRES_USER              = data.terraform_remote_state.rds.outputs.db_username
    POSTGRES_PASSWORD          = var.db_password
    SPRING_DATASOURCE_USERNAME = data.terraform_remote_state.rds.outputs.db_username
    SPRING_DATASOURCE_PASSWORD = var.db_password
  }

  type = "Opaque"
}
