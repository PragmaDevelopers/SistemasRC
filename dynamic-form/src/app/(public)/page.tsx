import Link from "next/link"

export default function Home(){
    return (
        <>
            <div className="mx-4">
                <Link className="text-blue-500" href={"./signup"}>Link para a área cadastro</Link>
            </div>
            <div className="mx-4">
                <Link className="text-blue-500" href={"./pdf_page/edit"}>Link para a área de pdf</Link>
            </div>
        </>
    )
}