"use client";
import React, { ReactNode, useState } from "react";
import InputsInterface from "./Interface/InputsInterface";
import CepDataInterface from "./Interface/CepData";
import { useForm, SubmitHandler } from "react-hook-form";
import {
  PowerOfAttorney,
  Ocuppation,
  Nationality,
  MaritalStatus,
  UfForRG,
  IssuingBody,
  StateForAddress,
  City,
  // AddressType,
  Neighborhood,
  AddressName,
  UfForCTPS
} from "./InputSelect/InputSelect";
import { FullName, Email, RG,CPF,MotherName,CEP,CTPSn,CTPSserie } from "./InputText/InputText";
import { CommonLawMarriage,AddressComplement } from "./InputRadio/InputRadio";
import { getAddressManually, tryGetAddressByCep } from "@/app/utils/handleError";

interface AccordionItemProps {
  title: string;
  children: ReactNode;
  isOpen?: boolean; // Adicione a propriedade isOpen para controlar o estado inicial do accordion
}

const AccordionItem: React.FC<AccordionItemProps> = ({ title, children, isOpen = true }) => {
  const [isAccordionOpen, setIsAccordionOpen] = useState(isOpen);

  return (
    <div>
      <button type="button" onClick={() => setIsAccordionOpen(!isAccordionOpen)}>{title}</button>
      {isAccordionOpen && <div>{children}</div>}
    </div>
  );
};

export default function UserForm() {
  const { register, handleSubmit, watch,setValue } = useForm<InputsInterface>({
    defaultValues: {
      cepNotFound: false,
    },
  });

  const onSubmit: SubmitHandler<InputsInterface> = (data) => console.log(data);
  
  const [cepData,setCepData] = useState<CepDataInterface>({
    uf: [],
    localidade: [],
    bairro: [],
    logradouro: []
  });
  
  React.useEffect(()=>{
    async function awaitFunction(){
       const response = await tryGetAddressByCep(watch);
       setCepData(response);
    }
    awaitFunction();
  },[watch().cep])

  return (
    <form style={{ width: "700px", margin: "0 auto" }} onSubmit={handleSubmit(onSubmit)}>
      <div>
        <AccordionItem title="Campos gerais" isOpen={true}>
          <PowerOfAttorney marginBottom={10} register={register} />
          <FullName marginBottom={10} register={register} />
          <Ocuppation marginBottom={10} register={register} />
          <Nationality marginBottom={10} register={register} />
          <Email marginBottom={10} register={register} />
        </AccordionItem>
      </div>
      <div>
        <AccordionItem title="Campos de documentos">
          <MaritalStatus marginBottom={10} register={register} />
          <CommonLawMarriage marginBottom={10} register={register} />
          <RG marginBottom={10} register={register} />
          <UfForRG marginBottom={10} register={register} />
          <IssuingBody marginBottom={10} register={register} />
          <CPF marginBottom={10} register={register} />
          <MotherName marginBottom={10} register={register} />
        </AccordionItem>
      </div>
      {
          ["previdenciario","civel","administrativo"].includes(watch().power_of_attorney) && (
          <div>
            <AccordionItem title="Campos extras">
              <CEP marginBottom={10} register={register} />
              <StateForAddress marginBottom={10} register={register} watch={watch} setValue={setValue} apiInfo={cepData.uf} />
              <City marginBottom={10} register={register} watch={watch} setValue={setValue} apiInfo={cepData.localidade} />
              <Neighborhood marginBottom={10} register={register} watch={watch} setValue={setValue} apiInfo={cepData.bairro} />
              {/* <AddressType marginBottom={10} register={register} watch={watch} /> */}
              {
                watch().cepNotFound && 
                  (
                    <button type="button" onClick={async()=>{setCepData(await getAddressManually(watch))}}
                    >Buscar Endereço</button>
                  )
              }
              <AddressName marginBottom={10} register={register} watch={watch} setValue={setValue} apiInfo={[...cepData?.logradouro]} />
              {["previdenciario","civel"].includes(watch().power_of_attorney) && (
                <AddressComplement marginBottom={10} register={register} watch={watch} />
              )}
            </AccordionItem>
          </div>
        )
      }
      {
        ["trabalhista"].includes(watch().power_of_attorney) && (
          <AccordionItem title="Campos extras">
            <CTPSn marginBottom={10} register={register} />
            <CTPSserie marginBottom={10} register={register} />
            <UfForCTPS marginBottom={10} register={register} />
          </AccordionItem>
        )
      }
      <br />
      <button>Enviar</button>
    </form>
  );
}
