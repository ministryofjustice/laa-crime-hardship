{{/* vim: set filetype=mustache: */}}
{{/*
Environment variables for service containers
*/}}
{{- define "laa-crime-hardship.env-vars" }}
env:
  - name: AWS_REGION
    value: {{ .Values.aws_region }}
  - name: SENTRY_DSN
    valueFrom:
      secretKeyRef:
        name: sentry-dsn
        key: SENTRY_DSN
  - name: SENTRY_ENV
    value: {{ .Values.java.host_env }}
  - name: SENTRY_SAMPLE_RATE
    value: {{ .Values.sentry.sampleRate | quote }}
  - name: LOG_LEVEL
    value: {{ .Values.logging.level }}
  - name: MAAT_API_BASE_URL
    value: {{ .Values.maatApi.baseUrl }}
  - name: MAAT_API_OAUTH_URL
    value: {{ .Values.maatApi.oauthUrl }}
  - name: CMA_API_BASE_URL
    value: {{ .Values.cmaApi.baseUrl }}
  - name: CMA_API_OAUTH_URL
    value: {{ .Values.cmaApi.oauthUrl }}
  - name: CMA_API_OAUTH_CLIENT_ID
    valueFrom:
      secretKeyRef:
        name: cma-api-oauth-client-id
        key: CMA_API_OAUTH_CLIENT_ID
  - name: CMA_API_OAUTH_CLIENT_SECRET
    valueFrom:
      secretKeyRef:
        name: cma-api-oauth-client-secret
        key: CMA_API_OAUTH_CLIENT_SECRET
  - name: MAAT_API_OAUTH_CLIENT_ID
    valueFrom:
      secretKeyRef:
        name: maat-api-oauth-client-id
        key: MAAT_API_OAUTH_CLIENT_ID
  - name: MAAT_API_OAUTH_CLIENT_SECRET
    valueFrom:
      secretKeyRef:
        name: maat-api-oauth-client-secret
        key: MAAT_API_OAUTH_CLIENT_SECRET
  - name: JWT_ISSUER_URI
    value: {{ .Values.jwt.issuerUri }}
{{- end -}}
