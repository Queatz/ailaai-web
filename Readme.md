Run
=====

`./gradlew jsBrowserRun`

Build
=====

`./gradlew jsBrowserProductionWebpack`

Files are in `build/distributions`

Deploy
=====

```shell
apt update
apt install certbot nodejs npm nginx python3-certbot-nginx
```

## HTTP -> HTTPS

1. Configure Nginx

2. Replace the contents of `/etc/nginx/sites-enabled/default` with the following

```
server {
    server_name <enter server host>;
    root /root/ui;
    listen 80;

    location / {
        index index.html;
        try_files $uri $uri/ /index.html =404;
    }
}
```

`chmod 755 /root`

3. Finally

```shell
certbot --nginx
nginx -t
service nginx restart
```
