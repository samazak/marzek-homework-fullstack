<script setup lang="ts" generic="T">
export interface DataTableColumn<T> {
  key: keyof T & string
  label: string
  sortable?: boolean
  rowHeader?: boolean
  cellClass?: string
}

withDefaults(defineProps<{
  columns: DataTableColumn<T>[]
  rows: T[]
  rowKey: keyof T & string
  caption?: string
  sortBy?: string
  sortDir?: 'asc' | 'desc'
  sortDisabled?: boolean
}>(), { sortDir: 'asc', sortDisabled: false })

const emit = defineEmits<{ sort: [key: string] }>()
</script>

<template>
  <div class="table-scroll">
    <table>
      <caption v-if="caption">{{ caption }}</caption>
      <thead>
        <tr>
          <th
            v-for="column in columns"
            :key="column.key"
            scope="col"
            :aria-sort="!column.sortable ? undefined : sortBy === column.key ? (sortDir === 'desc' ? 'descending' : 'ascending') : 'none'"
          >
            <button
              v-if="column.sortable"
              type="button"
              class="sort-button"
              :disabled="sortDisabled"
              @click="emit('sort', column.key)"
            >
              {{ column.label }}
              <span class="sort-indicator" aria-hidden="true">{{
                sortBy === column.key ? (sortDir === 'desc' ? '▼' : '▲') : ''
              }}</span>
            </button>
            <template v-else>{{ column.label }}</template>
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in rows" :key="String(row[rowKey])">
          <component
            :is="column.rowHeader ? 'th' : 'td'"
            v-for="column in columns"
            :key="column.key"
            :scope="column.rowHeader ? 'row' : undefined"
            :class="column.cellClass"
          >
            <slot :name="`cell-${column.key}`" :row="row">{{ row[column.key] }}</slot>
          </component>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.sort-button {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  padding: 0;
  font: inherit;
  font-weight: inherit;
  color: inherit;
  text-transform: inherit;
  letter-spacing: inherit;
  cursor: pointer;
}
.sort-button:hover, .sort-button:focus-visible { color: var(--color-primary); }
.sort-button:disabled { cursor: not-allowed; opacity: 0.6; }
.sort-indicator { display: inline-block; width: 0.8em; }
</style>
