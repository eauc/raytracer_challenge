FROM eauc/emacs-org as tangle

WORKDIR /app
COPY ./org ./org
RUN mkdir -p doc src/rt_clj test/rt_clj
RUN emacs --batch -l "/root/.emacs.d/init.el" \
    --eval "(tangle-all \"org\")" \
    --eval "(publish-all \"RayTracer Challenge - Clojure\" \"org\" \"doc\")"
RUN cp /root/theme.css /app/doc/

FROM nginx as production

COPY --from=tangle /app/doc /usr/share/nginx/html
COPY ./build/nginx.conf.template /etc/nginx/conf.d/conf.template

ENV PORT 80

CMD /bin/bash -c "cat /etc/nginx/conf.d/conf.template | envsubst > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"
