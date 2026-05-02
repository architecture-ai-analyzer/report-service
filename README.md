# 📊 Report Service — Microserviço de Análise de Arquitetura

Serviço responsável por gerenciar relatórios de análise de arquitetura de diagramas. Este microserviço processa resultados de IA, armazena relatórios e expõe APIs para consulta.

## 🏗️ Arquitetura

- **Porta:** 8083
- **Banco:** PostgreSQL (database: `reports`)
- **Queue:** AWS SQS para comunicação assíncrona
- **Framework:** Spring Boot 3.5.5 com Java 21

## 📁 Estrutura do Projeto

```
report-service/
├── src/
│   ├── main/
│   │   ├── java/com/fiap/report/
│   │   │   ├── domain/          # Entidades de domínio
│   │   │   ├── usecase/         # Casos de uso
│   │   │   ├── gateway/         # Interfaces de gateway
│   │   │   ├── repository/      # Repositórios JPA
│   │   │   ├── converter/       # JPA converters
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── listener/        # SQS listeners
│   │   │   └── dto/             # DTOs
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/changelog/    # Liquibase migrations
│   └── test/
├── k8s/                         # Kubernetes manifests
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## 🚀 Como Executar

### Desenvolvimento Local

1. **Com Docker Compose:**
```bash
docker-compose up -d
```

2. **Manualmente:**
```bash
# Iniciar o banco
docker-compose up -d db

# Build e execução
mvn clean package
java -jar target/report-service-*.jar
```

### Build da Imagem Docker

```bash
# Build
docker build -t report-service:latest .

# Run
docker run -p 8083:8083 report-service:latest
```

## 📊 Endpoints da API

### Relatórios
- `GET /api/reports/{id}` - Buscar relatório por ID
- `GET /api/reports/diagram/{diagramId}` - Buscar por diagram ID
- `GET /api/reports/user/{userId}` - Listar relatórios do usuário (paginado)
- `GET /api/reports/{id}/summary` - Resumo do relatório
- `DELETE /api/reports/{id}` - Excluir relatório

### Health Check
- `GET /actuator/health` - Status do serviço

## 🔧 Configuração

### Variáveis de Ambiente

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/reports
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=123

# AWS
AWS_ACCESS_KEY=your-access-key
AWS_SECRET_KEY=your-secret-key
AWS_REGION=us-east-1

# SQS
REPORT_GENERATION_QUEUE=report-generation-queue
STATUS_UPDATE_QUEUE=status-update-queue
```

## 🗄️ Database Schema

O serviço usa Liquibase para gerenciamento de schema:

- **Tabela:** `analysis_reports`
- **Campos JSON:** `components`, `risks`, `recommendations`
- **Índices:** `diagram_id`, `user_id`, `generated_at`

## 🔗 Integrações

### SQS Queues
- **Recebe:** Mensagens de geração de relatório do AI Service
- **Envia:** Atualizações de status para o Upload Service

### Fluxo de Processamento
1. AI Service envia resultado para `report-generation-queue`
2. Report Service processa e salva no PostgreSQL
3. Status atualizado via `status-update-queue`

## 🧪 Testes

```bash
# Executar todos os testes
mvn test

# Testes com container
mvn test -Ptestcontainers
```

## 📈 Monitoramento

- **Health:** `/actuator/health`
- **Metrics:** `/actuator/metrics`
- **Info:** `/actuator/info`

## 🚀 Deploy em Produção

### Kubernetes

```bash
# Aplicar manifests
kubectl apply -f k8s/

# Verificar deployment
kubectl get pods -l app=report-service
```

### Variáveis de Produção
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5432/reports
SPRING_JPA_HIBERNATE_DDL_AUTO=none

# AWS (via IAM Role ou credentials)
AWS_REGION=us-east-1
```

## 🔒 Segurança

- **Autenticação:** JWT tokens (validados via Upload Service)
- **Autorização:** Baseada em user_id dos relatórios
- **CORS:** Configurado para domínios específicos

## 📝 Logs

Formato estruturado JSON:
```json
{
  "timestamp": "2024-03-21T10:30:00.000Z",
  "level": "INFO",
  "service": "report-service",
  "message": "Report created successfully",
  "traceId": "abc123",
  "userId": "user-123",
  "reportId": "report-456"
}
```

## 🤝 Contribuição

1. Fork do projeto
2. Feature branch: `git checkout -b feature/nova-funcionalidade`
3. Commit: `git commit -m 'Add nova funcionalidade'`
4. Push: `git push origin feature/nova-funcionalidade`
5. Pull Request

## 📄 Licença

MIT License - ver arquivo LICENSE
