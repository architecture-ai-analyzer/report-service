output "challengeone_service_cluster_ip" {
  description = "ClusterIP do Service para referência interna (opcional)"
  value       = kubernetes_service.challengeone_app.spec[0].cluster_ip
}

output "challengeone_lb_hostname" {
  description = "DNS do Load Balancer interno"
  value = try(kubernetes_service.challengeone_app.status[0].load_balancer[0].ingress[0].hostname, "Aguardando DNS...")
}