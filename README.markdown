# sorty

Simple, web-based, keyboard-driven classifier.

## Architecture

- Pedestal server
- Fulcro SPA front-end
- Postgres storage (though this should be pluggable)
- Session management with Redis?

## Affordances

- Devcards for UI development
- Emacs-based repls for Clojure and ClojureScript
- Easy versioning, building, packaging
- use boot or leiningen where it's best-served
- Suitable for deployment to AWS
- Dockerized?

## Development

Starting server in dev

```bash
lein run-dev
```

```bash
curl http://localhost:8765/hallo
```

Or, using [`reloaded.repl`](https://github.com/weavejester/reloaded.repl):

```clojure
(require
  '[com.grzm.sorty.server.reloaded :as reloaded]
  '[reloaded.repl])
(reloaded/init!)
(reloaded.repl/go)
```

## Testing

To test the server backend

```bash
lein test
```
