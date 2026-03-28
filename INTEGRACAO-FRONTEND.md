# 🚀 Integração Frontend + Report Service

## 📋 Análise do Frontend Atual

O frontend **secure-systems-frontend** utiliza:
- **React 18** com **Vite** como bundler
- **React Router** para navegação
- **Tailwind CSS** para estilização
- **MockApiService** para dados simulados

## 🎯 Endpoints Necessários

Baseado na análise do frontend, o backend precisa fornecer:

### 1. **POST /api/reports/{uploadId}**
```json
// Request Body
{
  "userId": "user-123",
  "fileName": "diagram.pdf"
}

// Response (200)
{
  "id": "uuid",
  "diagramId": "uuid", 
  "userId": "user-123",
  "components": [...],
  "risks": [...],
  "recommendations": [...],
  "status": "COMPLETED",
  "createdAt": "2026-03-25T02:00:00"
}
```

### 2. **GET /api/reports/{uploadId}/status**
```json
// Response (200)
{
  "id": "upload-123",
  "status": "ANALISADO",
  "progress": 100,
  "estimatedTimeRemaining": "0 minutos",
  "currentStep": "Análise concluída"
}
```

### 3. **GET /api/reports/{uploadId}**
```json
// Response (200) - Relatório completo
{
  "id": "uuid",
  "components": [...],
  "risks": [...], 
  "recommendations": [...],
  "summary": {...}
}
```

### 4. **GET /api/reports**
```json
// Response (200) - Lista de relatórios
[
  {
    "id": "uuid",
    "diagramId": "uuid",
    "userId": "user-123", 
    "status": "COMPLETED",
    "createdAt": "2026-03-25T02:00:00"
  }
]
```

### 5. **GET /api/reports/{uploadId}/download**
```http
// Response (200) - PDF download
Content-Type: application/pdf
Content-Disposition: attachment; filename=report-uuid.pdf
```

## 🔧 Como Integrar

### Passo 1: Substituir MockApiService
No frontend, substitua:
```javascript
// Remover
import { MockApiService } from '../services/mockApiService';

// Adicionar  
import ReportApiService from '../services/ReportApiService';
```

### Passo 2: Atualizar chamadas
```javascript
// Antes
const reportData = await MockApiService.generateReport(upload.id, upload.fileName);

// Depois
const reportData = await ReportApiService.generateReport(upload.id, upload.fileName);
```

### Passo 3: Configurar CORS
No backend, adicionar ao application.properties:
```properties
# CORS Configuration
spring.web.cors.allowed-origins=http://localhost:5173
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true
```

## 🧪 Testes

### 1. Start Backend
```bash
cd report-service
mvn spring-boot:run
# Rodará em http://localhost:8080
```

### 2. Start Frontend  
```bash
cd secure-systems-frontend
npm run dev
# Rodará em http://localhost:5173
```

### 3. Testar Integração
1. Acesse http://localhost:5173
2. Faça upload de um arquivo
3. Verifique se o backend recebe as requisições
4. Confirme geração do relatório

## 📊 Fluxo Completo

```
Frontend → POST /api/reports/{uploadId}
    ↓
Backend → Processa dados (mock IA)
    ↓  
Backend → Salva no PostgreSQL
    ↓
Frontend ← GET /api/reports/{uploadId}/status
    ↓
Frontend ← GET /api/reports/{uploadId} (relatório completo)
    ↓
Frontend ← GET /api/reports/{uploadId}/download
```

## 🎯 Benefícios

✅ **Dados reais** em vez de mocks  
✅ **Persistência** no PostgreSQL  
✅ **Integração completa** frontend/backend  
✅ **Escalabilidade** com arquitetura limpa  
✅ **Monitoramento** via logs centralizados  

## 📝 Próximos Passos

1. **Implementar paginação real** no endpoint GET /api/reports
2. **Adicionar autenticação** JWT nos endpoints
3. **Implementar geração real** de PDF
4. **Adicionar WebSockets** para status em tempo real
5. **Configurar ambiente** de produção
