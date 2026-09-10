import { apiClient, type ApiClient } from '../../api/client'

export interface PiStep { iteration: number; term: string; partialSum: string }
export interface PiResult { value: string; precision: string; steps: PiStep[] }

export async function pi(precision: string, client: ApiClient = apiClient): Promise<PiResult> {
  return client.request('/pi', {
    method: 'POST', headers: { 'Content-Type': 'application/json', ...await client.csrfHeader() },
    body: JSON.stringify({ precision }),
  })
}
