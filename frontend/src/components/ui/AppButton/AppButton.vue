<script setup lang="ts">
defineProps<{
  busy?: boolean
  label: string
  busyLabel?: string
}>()
</script>

<template>
  <button :disabled="busy" type="submit">
    {{ busy && busyLabel ? busyLabel : label }}
  </button>
</template>
