"use client";

import { PDFViewer } from "@react-pdf/renderer";
import PdfGenerator from "./components/PdfGenerator/PdfGenerator";

function PDFPage() {
  return (
    <PDFViewer width="100%" height={600}>
      <PdfGenerator />
    </PDFViewer>
  );
}

export default PDFPage;