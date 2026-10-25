export interface FacturaDato {
  idFactura: number;
  monto: number;
  fechaCompra: string;
  metodoPago: string;
  externalReference: string;
  // Número de operación de MercadoPago
  idPago: number | null;
  idUsuario: number;
  titulosLibrosComprados: string[];
}

const METODOS_DE_PAGO: Record<string, string> = {
  credit_card: 'Tarjeta de crédito',
  debit_card: 'Tarjeta de débito',
  prepaid_card: 'Tarjeta prepaga',
  account_money: 'Dinero en cuenta de MercadoPago',
  ticket: 'Pago en efectivo',
  bank_transfer: 'Transferencia bancaria',
};

export function nombreMetodoPago(metodo: string) {
  return METODOS_DE_PAGO[metodo] ?? metodo;
}
