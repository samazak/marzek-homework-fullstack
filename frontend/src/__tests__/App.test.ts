import { flushPromises, mount } from '@vue/test-utils'
import { describe, expect, it, vi } from 'vitest'
import { i18n } from '../i18n'
import App from '../App.vue'
import { logout, session } from '../features/login/api'

vi.mock('../features/login/api', () => ({ logout: vi.fn(), session: vi.fn() }))

describe('App sign out', () => {
  it('hides the authenticated view once the server confirms logout', async () => {
    vi.mocked(session).mockResolvedValue(true)
    vi.mocked(logout).mockResolvedValue(undefined)
    const wrapper = mount(App, { global: { plugins: [i18n] } })
    await flushPromises()

    await wrapper.find('.header-action').trigger('click')
    await flushPromises()

    expect(wrapper.find('.header-action').exists()).toBe(false)
  })

  it('keeps the authenticated view and shows an error when logout fails', async () => {
    vi.mocked(session).mockResolvedValue(true)
    vi.mocked(logout).mockRejectedValue(new Error('Sign out failed. Please try again.'))
    const wrapper = mount(App, { global: { plugins: [i18n] } })
    await flushPromises()

    await wrapper.find('.header-action').trigger('click')
    await flushPromises()

    expect(wrapper.find('.header-action').exists()).toBe(true)
    expect(wrapper.find('[role="alert"]').text()).toBe('Sign out failed. Please try again.')
  })
})
