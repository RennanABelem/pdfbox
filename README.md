# 📄 PDF Template Builder — Projeto Spring Boot + PDFBox

Este projeto permite **gerar documentos PDF dinâmicos** com base em **templates JSON**.  
Cada template define o conteúdo, estilo e layout do PDF, enquanto os dados são injetados dinamicamente via payload JSON.

---

## 🚀 Funcionalidades

- Renderização de PDFs a partir de templates JSON  
- Use ${variavel} para inserir dados dinâmicos vindos do payload.
- Adicione "blank" para espaçamento vertical entre blocos.
- Use "multi-column" para alinhar campos lado a lado.
- Definição flexível de layout: column, multi-column, signature e blank  
- Suporte a tamanho de fontes e tipos: normal ou bold. 

---

## 🧩 Estrutura do Template JSON

Abaixo está um exemplo de estrutura básica de um arquivo `template.json`:

```json
{
  "font-size": "10",
  "lines": [
    {
      "type": "column",
      "font": "Helvetica_Bold",
      "font-size": "14",
      "text-type": "center",
      "text-value": "Título do Documento"
    },
    {
      "type": "blank"
    },
    {
      "type": "column",
      "font": "Helvetica",
      "text-type": "left",
      "text-value": "Este é um parágrafo simples com texto estático."
    },
    {
      "type": "multi-column",
      "fields": [
        {
          "type": "column",
          "font": "Helvetica_Bold",
          "text-value": "Nome:",
          "axis-x": "0"
        },
        {
          "type": "column",
          "font": "Helvetica",
          "text-value": "${beneficiary.name}",
          "axis-x": "50"
        }
      ]
    },
    {
      "type": "blank"
    },
    {
      "type": "signature",
      "text": "Assinatura do responsável",
      "width": 200,
      "font": "Helvetica",
      "font-size": 8,
      "axis-x": 40
    }
  ]
}
