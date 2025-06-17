{{/*
Expand the name of the chart.
*/}}
{{- define "laa-crime-hardship.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "laa-crime-hardship.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "laa-crime-hardship.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create ingress configuration
*/}}
{{- define "laa-crime-hardship.ingress" -}}
{{- $internalAllowlistSourceRange := (lookup "v1" "Secret" .Release.Namespace "ingress-internal-allowlist-source-range").data.INTERNAL_ALLOWLIST_SOURCE_RANGE | b64dec }}
{{- if $internalAllowlistSourceRange }}
  nginx.ingress.kubernetes.io/whitelist-source-range: {{ $internalAllowlistSourceRange }}
{{- end }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "laa-crime-hardship.labels" -}}
helm.sh/chart: {{ include "laa-crime-hardship.chart" . }}
{{ include "laa-crime-hardship.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "laa-crime-hardship.selectorLabels" -}}
app.kubernetes.io/name: {{ include "laa-crime-hardship.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "laa-crime-hardship.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "laa-crime-hardship.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}
