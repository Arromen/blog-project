# Устанавливаем имя пользователя и пароль
ENV POSTGRES_USER=aboba1337
ENV POSTGRES_PASSWORD=password

# Устанавливаем имя базы данных
ENV POSTGRES_DB=article

# Создаем каталог для данных
VOLUME /var/lib/postgresql/data

# Запускаем PostgreSQL
CMD ["localhost"]