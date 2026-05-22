resource "kubernetes_config_map" "report_service" {
  metadata {
    name      = "${var.app_name}-config"
    namespace = kubernetes_namespace.report_service.metadata[0].name
  }

  data = {
    SPRING_APPLICATION_NAME     = var.app_name
    AWS_REGION                  = var.aws_region
    LOG_LEVEL                   = "INFO"
    SPRING_DATASOURCE_URL       = var.rds_jdbc_url
    SPRING_DATASOURCE_USERNAME  = var.rds_username
    AWS_STATUS_UPDATE_QUEUE_URL = var.aws_status_update_queue_url
  }

  depends_on = [
    kubernetes_namespace.report_service
  ]
}
