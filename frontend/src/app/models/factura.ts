export interface FacturaDato {
  idFactura: number;
  monto: number;
  fechaCompra: string;
  metodoPago: string;
  externalReference: string;
  idUsuario: number;
  titulosLibrosComprados: string[];
}
