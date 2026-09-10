<script setup lang="ts">
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { pi, type PiResult, type PiStep } from './api'
import { useAsyncAction } from '../../composables/useAsyncAction'
import AppButton from '../../components/ui/AppButton/AppButton.vue'
import ErrorMessage from '../../components/ui/ErrorMessage/ErrorMessage.vue'
import LabeledField from '../../components/ui/LabeledField/LabeledField.vue'
import PanelHeader from '../../components/ui/PanelHeader/PanelHeader.vue'
import ResultOutput from '../../components/ui/ResultOutput/ResultOutput.vue'
import DataTable, { type DataTableColumn } from '../../components/ui/DataTable/DataTable.vue'

const { t } = useI18n()
const columns = computed<DataTableColumn<PiStep>[]>(() => [
  { key: 'iteration', label: t('pi.columns.iteration') },
  { key: 'partialSum', label: t('pi.columns.partialSum') },
])
const precision = ref('0.000001')
const result = ref<PiResult>()
const { busy, error, run } = useAsyncAction()

async function submit() {
  result.value = await run(() => pi(precision.value))
}
</script>

<template>
  <section aria-labelledby="pi-title" class="card">
    <PanelHeader
      :eyebrow="t('pi.eyebrow')"
      :title="t('pi.title')"
      title-id="pi-title"
      :description="t('pi.description')"
    />

    <form @submit.prevent="submit">
      <div class="input-row">
        <LabeledField
          id="precision"
          v-model="precision"
          :label="t('pi.label')"
          type="number"
          min="0"
          step="any"
        />
        <AppButton :busy="busy" :label="t('pi.submit')" :busy-label="t('pi.submitting')" />
      </div>
    </form>

    <ErrorMessage :message="error" />
    <div v-if="result" aria-live="polite">
      <ResultOutput numeric>{{ result.value }}</ResultOutput>

      <details>
        <summary>{{ t('pi.details') }}</summary>
        <DataTable :columns="columns" :rows="result.steps" row-key="iteration" />
      </details>
    </div>
  </section>
</template>
