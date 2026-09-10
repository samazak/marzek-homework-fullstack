import { flushPromises, mount } from '@vue/test-utils'
import { describe, expect, it, vi } from 'vitest'
import { i18n } from '../../../i18n'
import AgePanel from '../AgePanel.vue'
import { age } from '../api'

vi.mock('../api', () => ({ age: vi.fn() }))

describe('AgePanel', () => {
  it('shows age text returned by the backend', async () => {
    vi.mocked(age).mockResolvedValue({ age: '18', text: 'You are adult' })
    const wrapper = mount(AgePanel, { global: { plugins: [i18n] } })

    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(wrapper.find('output').text()).toBe('You are adult')
  })

  it('shows errors and re-enables submission', async () => {
    vi.mocked(age).mockRejectedValue(new Error('Database unavailable'))
    const wrapper = mount(AgePanel, { global: { plugins: [i18n] } })

    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(wrapper.find('[role="alert"]').text()).toBe('Database unavailable')
    expect(wrapper.find('button').attributes('disabled')).toBeUndefined()
  })
})
