<script setup lang="ts">
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { invoices as fetchInvoices, type Invoice, type InvoicePage, type InvoiceSortKey } from './api'
import { useAsyncAction } from '../../composables/useAsyncAction'
import AppButton from '../../components/ui/AppButton/AppButton.vue'
import ErrorMessage from '../../components/ui/ErrorMessage/ErrorMessage.vue'
import PanelHeader from '../../components/ui/PanelHeader/PanelHeader.vue'
import DataTable, { type DataTableColumn } from '../../components/ui/DataTable/DataTable.vue'

const { t } = useI18n()
const page = ref<InvoicePage>()
const sortBy = ref<InvoiceSortKey>('invoiceId')
const sortDir = ref<'asc' | 'desc'>('asc')
const { busy, error, run } = useAsyncAction()

const columns = computed<DataTableColumn<Invoice>[]>(() => [
  { key: 'invoiceId', label: t('invoices.columns.invoice'), sortable: true, rowHeader: true },
  { key: 'invoiceDate', label: t('invoices.columns.date'), sortable: true },
  { key: 'currency', label: t('invoices.columns.currency'), sortable: true },
  { key: 'amount', label: t('invoices.columns.amount'), sortable: true },
  { key: 'paidAmount', label: t('invoices.columns.paid'), sortable: true },
  { key: 'outstandingAmount', label: t('invoices.columns.outstanding'), sortable: true, cellClass: 'balance' },
])

async function load(offset = 0) {
  const loaded = await run(() => fetchInvoices(offset, sortBy.value, sortDir.value))
  if (loaded) page.value = loaded
}

function sortByColumn(key: string) {
  if (sortBy.value === key) {
    sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortBy.value = key as InvoiceSortKey
    sortDir.value = 'asc'
  }
  load(0)
}
</script>

<template>
  <section aria-labelledby="invoice-title" class="card invoices">
    <PanelHeader
      :eyebrow="t('invoices.eyebrow')"
      :title="t('invoices.title')"
      title-id="invoice-title"
      :description="t('invoices.description')"
    />

    <AppButton :busy="busy" :label="t('invoices.submit')" :busy-label="t('invoices.submitting')" @click="load()" />
    <ErrorMessage :message="error" />

    <div v-if="page" aria-live="polite">
      <p v-if="page.items.length === 0">{{ t('invoices.empty') }}</p>
      <DataTable
        v-else
        :columns="columns"
        :rows="page.items"
        row-key="invoiceId"
        :caption="t('invoices.caption')"
        :sort-by="sortBy"
        :sort-dir="sortDir"
        :sort-disabled="busy"
        @sort="sortByColumn"
      >
        <template #cell-invoiceId="{ row }">#{{ row.invoiceId }}</template>
      </DataTable>
      <AppButton
        v-if="page.nextOffset !== null"
        :busy="busy"
        :label="t('invoices.nextPage')"
        @click="load(page.nextOffset!)"
      />
    </div>
  </section>
</template>
