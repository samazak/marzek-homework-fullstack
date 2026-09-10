import { describe, expect, it, vi } from 'vitest'
import { ApiClient } from '../../../api/client'
import { pi } from '../api'

describe('pi', () => {
  it('sends the CSRF token and preserves decimal precision as text', async () => {
    const transport = vi.fn<typeof fetch>()
      .mockResolvedValueOnce(new Response(JSON.stringify({ headerName: 'X-CSRF-TOKEN', token: 'test-token' })))
      .mockResolvedValueOnce(new Response(JSON.stringify({ value: '3.14159', precision: '0.000001', steps: [] })))

    await pi('0.000001', new ApiClient(transport))

    expect(transport.mock.calls[1][1]).toMatchObject({
      method: 'POST', credentials: 'same-origin',
      headers: { 'X-CSRF-TOKEN': 'test-token' }, body: '{"precision":"0.000001"}',
    })
  })
})
