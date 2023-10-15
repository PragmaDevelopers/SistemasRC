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
import { getAddressManually, tryGetAddressByCep } from "@/utils/handleError";

interface AccordionItemProps {
  title: string;
  children: ReactNode;
  isOpen?: boolean; // Adicione a propriedade isOpen para controlar o estado inicial do accordion
}

const AccordionItem: React.FC<AccordionItemProps> = ({ title, children, isOpen = true }) => {
  const [isAccordionOpen, setIsAccordionOpen] = useState(isOpen);

  return (
    <div>
      <button className="text-xl font-bold mb-3" type="button" onClick={() => setIsAccordionOpen(!isAccordionOpen)}>{title}</button>
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
  
  React.useEffect(()=>{
    async function awaitFunction(){
       const response = await tryGetAddressByCep(watch);
       setCepData(response);
    }
    awaitFunction();
  },[watch().cep])

  const [cepData,setCepData] = useState<CepDataInterface>({
    uf: [""],
    localidade: [""],
    bairro: [""],
    logradouro: [""]
  });

  return (
    <form className="bg-white mx-auto w-full max-w-lg my-3" onSubmit={handleSubmit(onSubmit)}>
      <div className="bg-gray-200 p-5 border-b-2 border-gray-400">
        <AccordionItem title="Campos gerais" isOpen={true}>
          <PowerOfAttorney className={"mb-3"} register={register} />
          <FullName className={"mb-3"} register={register} />
          <Ocuppation className={"mb-3"} register={register} />
          <Nationality className={"mb-3"} register={register} />
          <Email className={""} register={register} />
        </AccordionItem>
      </div>
      <div className="bg-gray-200 p-5 border-b-2 border-gray-400">
        <AccordionItem title="Campos de documentos">
          <MaritalStatus className={"mb-3"} register={register} />
          <CommonLawMarriage className={"mb-3"} register={register} />
          <RG className={"mb-3"} register={register} />
          <UfForRG className={"mb-3"} register={register} />
          <IssuingBody className={"mb-3"} register={register} />
          <CPF className={"mb-3"} register={register} />
          <MotherName className={""} register={register} />
        </AccordionItem>
      </div>
      {
          ["previdenciario","civel","administrativo"].includes(watch().power_of_attorney) && (
          <div className="bg-gray-200 p-5 border-b-2 border-gray-400">
            <AccordionItem title="Campos extras">
              <CEP className={"mb-3"} register={register} />
              <StateForAddress className={"mb-3"} register={register} watch={watch} setValue={setValue} apiInfo={cepData.uf} />
              <City className={"mb-3"} register={register} watch={watch} setValue={setValue} apiInfo={cepData.localidade} />
              <Neighborhood className={"mb-3"} register={register} watch={watch} setValue={setValue} apiInfo={cepData.bairro} />
              {/* <AddressType className={"mb-3"} register={register} watch={watch} /> */}
              {
                watch().cepNotFound && 
                  (
                    <div className="border-b-2 border-gray-400 mb-3">
                      <button className="bg-white py-2 px-3 mb-3" type="button" onClick={async()=>{setCepData(await getAddressManually(watch))}}
                      >Buscar Endereço</button>
                    </div>
                  )
              }
              <AddressName className={"mb-3"} register={register} watch={watch} setValue={setValue} apiInfo={cepData.logradouro} />
              {["previdenciario","civel"].includes(watch().power_of_attorney) && (
                <AddressComplement className={"mb-3"} register={register} watch={watch} />
              )}
            </AccordionItem>
          </div>
        )
      }
      {
          ["trabalhista"].includes(watch().power_of_attorney) && (
            <div className="bg-gray-200 p-5 border-b-2 border-gray-400">
                <AccordionItem title="Campos extras">
                  <CTPSn className={"mb-3"} register={register} />
                  <CTPSserie className={"mb-3"} register={register} />
                  <UfForCTPS className={"mb-3"} register={register} />
                </AccordionItem>
            </div>
          )
      }
      <div className="bg-gray-200 p-5 border-b-2 border-gray-400">
        <button className="bg-white py-2 px-3" type="submit">Enviar</button>
      </div>
    </form>
  );
}
