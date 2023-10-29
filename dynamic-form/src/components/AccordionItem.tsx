import { ReactNode, useState } from "react";

export interface AccordionItemProps {
    title: string;
    children: ReactNode;
    isOpen?: boolean; // Adicione a propriedade isOpen para controlar o estado inicial do accordion
}

export const AccordionItem: React.FC<AccordionItemProps> = ({ title, children, isOpen = true }) => {
    const [isAccordionOpen, setIsAccordionOpen] = useState(isOpen);
  
    return (
      <div>
        <button className="text-xl font-bold mb-3" type="button" onClick={() => setIsAccordionOpen(!isAccordionOpen)}>{title}</button>
        {isAccordionOpen && <div>{children}</div>}
      </div>
    );
  };