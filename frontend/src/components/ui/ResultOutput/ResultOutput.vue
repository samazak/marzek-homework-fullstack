<script setup lang="ts">
import CheckIcon from '../../../assets/icons/CheckIcon.vue'

defineProps<{ numeric?: boolean }>()
</script>

<template>
  <output aria-live="polite" class="result" :class="{ numeric }">
    <CheckIcon />
    <span><slot /></span>
  </output>
</template>
