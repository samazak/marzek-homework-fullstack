import { flushPromises, mount } from '@vue/test-utils'
import { describe, expect, it, vi } from 'vitest'
import { i18n } from '../../../i18n'
import PiPanel from '../PiPanel.vue'
import { pi } from '../api'

vi.mock('../api', () => ({ pi: vi.fn() }))

describe('PiPanel', () => {
  it('renders pi and its saved steps', async () => {
    vi.mocked(pi).mockResolvedValue({
      value: '3.14159', precision: '0.000001', steps: [
        { iteration: 0, term: '3.13', partialSum: '3.13' },
      ],
    })
    const wrapper = mount(PiPanel, { global: { plugins: [i18n] } })

    await wrapper.find('input').setValue('0.000000000001234567890123')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(pi).toHaveBeenCalledWith('0.000000000001234567890123')
    expect(wrapper.find('output').text()).toBe('3.14159')
    expect(wrapper.findAll('tbody tr')).toHaveLength(1)
  })
})
