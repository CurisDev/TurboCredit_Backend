Crear y poner en el archivo `.env` las variables de conexión:

```bash
DB_URL=jdbc:postgresql://localhost:5432/turbo-credit-db
DB_USER=admin
DB_PASSWORD=admin
```

Comandos básicos para manejar el contenedor con Docker Compose:

- Levantar (en segundo plano):

```bash
docker compose up -d
```

- Apagar y eliminar los contenedores (sin borrar volúmenes):

```bash
docker compose down
```

- Apagar y eliminar contenedores y volúmenes (reset total):

```bash
docker compose down -v
```

- Ver el estado de los servicios/containers (equivalente a "ps"):

```bash
docker compose ps
```

Ejemplo de salida (tu ejemplo):

```
NAME                        IMAGE         COMMAND                  SERVICE    CREATED          STATUS          PORTS
turbo-credit-db-containes   postgres:18   "docker-entrypoint.s…"   postgres   58 seconds ago   Up 57 seconds   0.0.0.0:5432->5432/tcp, [::]:5432->5432/tcp
```

Nota importante sobre la columna PORTS:

- La parte que aparece como

```
0.0.0.0:5432->5432/tcp, [::]:5432->5432/tcp
```

indica que el puerto 5432 del contenedor está publicado en el puerto 5432 del host y aceptará conexiones desde cualquier IP (IPv4 0.0.0.0 y IPv6 [::]). Si la parte PORTS está vacía o no contiene esa entrada, tu servicio no estará accesible desde el host en ese puerto.

Qué revisar si no aparece la publicación del puerto:
1. Asegúrate de tener en tu `docker-compose.yml` la sección `ports` para el servicio postgres, por ejemplo:

```yaml
ports:
  - "5432:5432"
```

2. Comprueba que el contenedor realmente está corriendo y el servicio está listo:

```bash
docker compose logs -f <nombre_servicio_o_contenedor>
```

3. Verifica en el host que el puerto está escuchando (ejemplo Linux):

```bash
ss -ltnp | grep 5432
# o
sudo lsof -i :5432
```

4. Si el puerto no aparece y usas un firewall, abre el puerto o ajusta reglas (ej. `ufw allow 5432/tcp`).

Comandos útiles adicionales:

- Entrar al contenedor y usar psql (si la imagen tiene psql instalado):

```bash
docker exec -it turbo-credit-db-containes psql -U $DB_USER -d turbo-credit-db
```

- Ver logs en tiempo real:

```bash
docker compose logs -f
```

Con esto podrás levantar, apagar y comprobar el estado del contenedor y confirmar que el puerto 5432 está correctamente expuesto y accesible desde el host.

