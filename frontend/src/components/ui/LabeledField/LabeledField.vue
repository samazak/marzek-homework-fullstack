<script setup lang="ts">
withDefaults(defineProps<{
  id: string
  label: string
  modelValue: string
  type?: string
  min?: string
  max?: string
  step?: string
  autocomplete?: string
  required?: boolean
  autofocus?: boolean
}>(), {
  type: 'text',
  required: true,
})

defineEmits<{ (e: 'update:modelValue', value: string): void }>()
</script>

<template>
  <label :for="id">{{ label }}</label>
  <input
    :id="id"
    :value="modelValue"
    @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
    :type="type"
    :min="min"
    :max="max"
    :step="step"
    :autocomplete="autocomplete"
    :required="required"
    :autofocus="autofocus"
  >
</template>
