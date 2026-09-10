import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

/** Shared busy/error handling so panels only describe what to call, not how to guard it. */
export function useAsyncAction() {
  const { t } = useI18n()
  const busy = ref(false)
  const error = ref('')

  async function run<T>(action: () => Promise<T>): Promise<T | undefined> {
    busy.value = true
    error.value = ''
    try {
      return await action()
    } catch (e) {
      error.value = e instanceof Error ? e.message : t('errors.requestFailed')
      return undefined
    } finally {
      busy.value = false
    }
  }

  return { busy, error, run }
}
