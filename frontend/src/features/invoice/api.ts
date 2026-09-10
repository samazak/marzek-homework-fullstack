import { apiClient, type ApiClient } from '../../api/client'

export interface Invoice {
  invoiceId: string; invoiceDate: string; currency: string
  amount: string; paidAmount: string; outstandingAmount: string
}
export type InvoiceSortKey = 'invoiceId' | 'invoiceDate' | 'currency' | 'amount' | 'paidAmount' | 'outstandingAmount'
export interface InvoicePage { items: Invoice[]; nextOffset: number | null }

export function invoices(
  offset = 0, sortBy: InvoiceSortKey = 'invoiceId', sortDir: 'asc' | 'desc' = 'asc', client: ApiClient = apiClient,
): Promise<InvoicePage> {
  return client.request(
    `/invoices/unpaid?offset=${offset}&limit=25&sortBy=${sortBy}&sortDir=${sortDir}`,
  )
}
