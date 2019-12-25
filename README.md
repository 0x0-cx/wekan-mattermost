# Wekan-Mattermost

![](https://github.com/razum2um/wekan-mattermost/workflows/Clojure%20CI/badge.svg)

An server which listens to outgoing [Wekan](https://wekan.github.io/) http [hooks](https://github.com/wekan/wekan/wiki/Webhook-data)
and posts them to a [Mattermost](https://mattermost.com/) channel

## Howto

- [Get mattermost incoming url](https://docs.mattermost.com/developer/webhooks-incoming.html#simple-incoming-webhook)
- Run `docker run -e OUT_URL=https://mattermost.url/hooks/... -e PORT=3434 -p 3434:3434 razum2um/wekan-mattermost`
- [Configure wekan outgoing webhook url](https://github.com/wekan/wekan/wiki/Outgoing-Webhook-to-Discord) to exposed server

*NOTE:* there's no auth, treat integration server url secret (same as you should treat mattemost incoming urls)

## Features

- prepocesses text and replaces board name & url to a link
