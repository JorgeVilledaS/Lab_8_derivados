#!/bin/bash
# Configura DNS de Google con DNS-over-TLS en Fedora

set -e

echo "🧩 Detectando interfaz de red activa..."
INTERFACE=$(nmcli -t -f DEVICE,STATE d | grep ':connected' | cut -d: -f1)

if [ -z "$INTERFACE" ]; then
  echo "❌ No se detectó ninguna interfaz de red activa. Conéctate a Internet primero."
  exit 1
fi

echo "✅ Interfaz detectada: $INTERFACE"

echo "🌐 Configurando DNS de Google..."
sudo nmcli device modify "$INTERFACE" ipv4.ignore-auto-dns yes
sudo nmcli device modify "$INTERFACE" ipv4.dns "8.8.8.8 8.8.4.4"

echo "🔒 Habilitando DNS-over-TLS (DNS sobre TLS)..."
sudo mkdir -p /etc/systemd/resolved.conf.d
sudo tee /etc/systemd/resolved.conf.d/dns-over-tls.conf > /dev/null <<EOF
[Resolve]
DNS=8.8.8.8 8.8.4.4
DNSOverTLS=yes
EOF

echo "♻️ Reiniciando servicios de red..."
sudo systemctl restart systemd-resolved
sudo nmcli networking off && sudo nmcli networking on

echo "✅ Configuración completa."

echo "📋 Verificación del estado:"
systemd-resolve --status | grep -E 'DNS Servers|DNS over TLS'

