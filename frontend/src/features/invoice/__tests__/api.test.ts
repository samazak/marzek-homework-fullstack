import { describe, expect, it, vi } from 'vitest'
import { ApiClient } from '../../../api/client'
import { invoices } from '../api'

describe('invoices', () => {
  it('sends pagination and sort parameters', async () => {
    const transport = vi.fn<typeof fetch>()
      .mockResolvedValue(new Response(JSON.stringify({ items: [], nextOffset: null })))

    await invoices(25, 'amount', 'desc', new ApiClient(transport))

    expect(transport.mock.calls[0][0]).toBe('/api/invoices/unpaid?offset=25&limit=25&sortBy=amount&sortDir=desc')
  })
})
