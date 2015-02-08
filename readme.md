java-config
===========

Light java configuration inspired by Netflix's archaius.

Tries to:

  * Keep the core light. Doesn't bake in lots of static config. If desired it can be provided
  by a layer on top or the container (say karyon). Also avoids baking that static configuration
  into various components making their dependencies less obvious.
  * Deal with whole configs rather than individual property changes. Best suited for cases where
  configuration changes are relatively infrequent and aren't using/abusing/constantly modifying
  configuration to pass application state around. Higher levels can derive individual changes
  if that's what they want.
  * Let schedulers just schedule, not apply the updates and event logic and what else.
  * Minimize baggage, no commons config
  * Be overly optimistic about how light it can be while actually solving real problems
