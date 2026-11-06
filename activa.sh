
#!/usr/bin/env bash

cd "$(dirname "$0")" || exit 1

mvn -q dependency:copy-dependencies -DoutputDirectory=target/dependency

# Pedimos la contraseña si no está configurada.
if [[ -z "${ACTIVA_DB_PASSWORD:-}" ]]; then
    read -rsp "Contraseña MySQL de activa: " ACTIVA_DB_PASSWORD
    echo
    export ACTIVA_DB_PASSWORD
fi

# Lanzamos nuestra aplicación Java.
java -Dorg.jboss.logging.provider=jdk \
     -Djava.util.logging.config.file=logging.properties \
     -cp "target/classes:target/dependency/*" \
     eus.birt.dam.ad04.main.Main
