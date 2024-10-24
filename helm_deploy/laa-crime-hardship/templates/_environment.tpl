{{/* vim: set filetype=mustache: */}}
{{/*
Environment variables for service containers
*/}}
{{- define "laa-crime-hardship.env-vars" }}
env:
  - name: AWS_REGION
    value: {{ .Values.aws_region }}
  - name: SENTRY_DSN
    value: {{ .Values.sentry.dsn }}
  - name: SENTRY_ENV
    value: {{ .Values.java.host_env }}
  - name: SENTRY_SAMPLE_RATE
    value: {{ .Values.sentry.sampleRate | quote }}
  - name: LOG_LEVEL
    value: {{ .Values.logging.level }}
  - name: MAAT_API_BASE_URL
    value: {{ .Values.maatApi.baseUrl }}
  - name: MAAT_API_REGISTRATION_ID
    value: {{ .Values.maatApi.registrationId }}
  - name: MAAT_API_OAUTH_URL
    value: {{ .Values.maatApi.oauthUrl }}
  - name: CMA_API_BASE_URL
    value: {{ .Values.cmaApi.baseUrl }}
  - name: CMA_API_REGISTRATION_ID
    value: {{ .Values.cmaApi.registrationId }}
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
  - name: DATASOURCE_HOST_PORT
    valueFrom:
      secretKeyRef:
        name: rds-postgresql-instance-output
        key: rds_instance_endpoint
  - name: DATASOURCE_DBNAME
    valueFrom:
      secretKeyRef:
        name: rds-postgresql-instance-output
        key: database_name
  - name: DATASOURCE_USERNAME
    valueFrom:
      secretKeyRef:
        name: rds-postgresql-instance-output
        key: database_username
  - name: DATASOURCE_PASSWORD
    valueFrom:
      secretKeyRef:
        name: rds-postgresql-instance-output
        key: database_password
{{- end -}}
