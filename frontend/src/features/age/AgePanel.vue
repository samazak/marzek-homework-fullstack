<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { age as fetchAge, type AgeResult } from './api'
import { useAsyncAction } from '../../composables/useAsyncAction'
import AppButton from '../../components/ui/AppButton/AppButton.vue'
import ErrorMessage from '../../components/ui/ErrorMessage/ErrorMessage.vue'
import LabeledField from '../../components/ui/LabeledField/LabeledField.vue'
import PanelHeader from '../../components/ui/PanelHeader/PanelHeader.vue'
import ResultOutput from '../../components/ui/ResultOutput/ResultOutput.vue'

const { t } = useI18n()
const age = ref('18')
const result = ref<AgeResult>()
const { busy, error, run } = useAsyncAction()

async function submit() {
  result.value = await run(() => fetchAge(age.value))
}
</script>

<template>
  <section aria-labelledby="age-title" class="card">
    <PanelHeader
      :eyebrow="t('age.eyebrow')"
      :title="t('age.title')"
      title-id="age-title"
      :description="t('age.description')"
    />

    <form @submit.prevent="submit">
      <div class="input-row">
        <LabeledField
          id="age"
          v-model="age"
          :label="t('age.label')"
          type="number"
          min="0"
          max="100000000000000000000"
          step="any"
        />
        <AppButton :busy="busy" :label="t('age.submit')" :busy-label="t('age.submitting')" />
      </div>
    </form>

    <ErrorMessage :message="error" />
    <ResultOutput v-if="result">{{ result.text }}</ResultOutput>
  </section>
</template>
