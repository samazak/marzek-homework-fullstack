<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { login } from './api'
import { useAsyncAction } from '../../composables/useAsyncAction'
import AppButton from '../../components/ui/AppButton/AppButton.vue'
import ErrorMessage from '../../components/ui/ErrorMessage/ErrorMessage.vue'
import LabeledField from '../../components/ui/LabeledField/LabeledField.vue'
import PanelHeader from '../../components/ui/PanelHeader/PanelHeader.vue'

const { t } = useI18n()
const emit = defineEmits<{ (e: 'authenticated'): void }>()

const username = ref('')
const password = ref('')
const { busy, error, run } = useAsyncAction()

async function submit() {
  const signedIn = await run(async () => {
    await login(username.value, password.value)
    return true
  })
  if (signedIn) emit('authenticated')
}
</script>

<template>
  <section aria-labelledby="login-title" class="card login-card">
    <PanelHeader
      :eyebrow="t('login.eyebrow')"
      :title="t('login.title')"
      title-id="login-title"
      :description="t('login.description')"
    />

    <form @submit.prevent="submit">
      <LabeledField
        id="username"
        v-model="username"
        :label="t('login.username')"
        autocomplete="username"
        autofocus
      />
      <LabeledField
        id="password"
        v-model="password"
        :label="t('login.password')"
        type="password"
        autocomplete="current-password"
      />

      <ErrorMessage :message="error" />
      <AppButton :busy="busy" :label="t('login.submit')" :busy-label="t('login.submitting')" />
    </form>
  </section>
</template>
