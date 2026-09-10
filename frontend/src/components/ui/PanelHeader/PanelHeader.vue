<script setup lang="ts">
defineProps<{
  eyebrow: string
  title: string
  titleId: string
  description?: string
}>()
</script>

<template>
  <p class="eyebrow">{{ eyebrow }}</p>
  <h2 :id="titleId">{{ title }}</h2>
  <p v-if="description">{{ description }}</p>
</template>
