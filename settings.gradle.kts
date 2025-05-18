rootProject.name = "kuke-board"

include(
    "common",
    "common:snowflake",
    "common:data-serializer",
    "common:event",
    "service",
    "service:article",
    "service:hot-article",
    "service:article-read",
    "service:view",
    "service:like",
    "service:comment"
)