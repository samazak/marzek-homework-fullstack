<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { logout, session } from './features/login/api'
import AgePanel from './features/age/AgePanel.vue'
import PiPanel from './features/pi/PiPanel.vue'
import InvoicePanel from './features/invoice/InvoicePanel.vue'
import LoginPanel from './features/login/LoginPanel.vue'
import BrandLogo from './assets/icons/BrandLogo.vue'
import ErrorMessage from './components/ui/ErrorMessage/ErrorMessage.vue'

const { t } = useI18n()
const authenticated = ref(false)
const checkingSession = ref(true)
const signOutError = ref('')

async function refreshSession() {
  try {
    authenticated.value = await session()
  } catch {
    authenticated.value = false
  }
}

onMounted(async () => {
  await refreshSession()
  checkingSession.value = false
})

async function signOut() {
  try {
    await logout()
    signOutError.value = ''
    authenticated.value = false
  } catch (e) {
    // Server session may still be active; do not claim signed-out state the backend never confirmed.
    signOutError.value = e instanceof Error ? e.message : t('brand.signOutFailed')
  }
}
</script>

<template>
  <header>
    <a class="brand" href="/">
      <BrandLogo />
      <span>{{ $t('brand.name') }}</span>
    </a>
    <button v-if="authenticated" class="header-action" @click="signOut">
      {{ $t('brand.signOut') }}
    </button>
  </header>

  <main>
    <div class="intro">
      <p class="eyebrow">{{ $t('intro.eyebrow') }}</p>
      <h1>{{ $t('intro.title') }}</h1>
      <p>{{ $t('intro.description') }}</p>
      <ErrorMessage v-if="authenticated" :message="signOutError" />
    </div>

    <LoginPanel v-if="!checkingSession && !authenticated" @authenticated="refreshSession" />
    <div v-else-if="authenticated" class="grid">
      <AgePanel />
      <PiPanel />
      <InvoicePanel />
    </div>
  </main>

  <footer>{{ $t('footer.text') }}</footer>
</template>
