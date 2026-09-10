import { flushPromises, mount } from '@vue/test-utils'
import { describe, expect, it, vi } from 'vitest'
import { i18n } from '../../../i18n'
import InvoicePanel from '../InvoicePanel.vue'
import { invoices } from '../api'

vi.mock('../api', () => ({ invoices: vi.fn() }))

describe('InvoicePanel', () => {
  it('handles an empty invoice page', async () => {
    vi.mocked(invoices).mockResolvedValue({ items: [], nextOffset: null })
    const wrapper = mount(InvoicePanel, { global: { plugins: [i18n] } })

    await wrapper.find('button').trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('No unpaid invoices found.')
  })
})
