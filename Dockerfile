FROM eauc/emacs-org as src

WORKDIR /app
RUN mkdir -p dev/rt_clj doc samples src/rt_clj test/rt_clj
COPY ./org ./org
RUN emacs --batch -l "/root/.emacs.d/init.el" \
    --eval "(tangle-all \"org\")" \
    --eval "(publish-all \"RayTracer Challenge - Clojure\" \"org\" \"doc\")"
RUN cp /root/.emacs.d/theme.css /app/doc/

COPY ./samples ./samples
COPY ./build/convert_ppm_images.sh .
RUN ./convert_ppm_images.sh

FROM nginx as production

COPY --from=src /app/doc /usr/share/nginx/html/doc
COPY --from=src /app/samples /usr/share/nginx/html/samples
COPY ./build/nginx.conf.template /etc/nginx/conf.d/conf.template

ENV PORT 80

CMD /bin/bash -c "cat /etc/nginx/conf.d/conf.template | envsubst > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"

FROM clojure:openjdk-8-lein-2.9.1 as test

WORKDIR /app

COPY ./project.clj .
COPY ./tests.edn .
RUN lein deps

COPY ./dev ./dev
COPY --from=src /app/src ./src
COPY --from=src /app/test ./test

CMD ["lein","kaocha"]
