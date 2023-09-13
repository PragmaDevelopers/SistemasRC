type Card = {

}

export default function Page({ params }: { params: { id: string } }) {
    const Cards: Card[] = []
    return (
        <main className="w-full h-full overflow-hidden">
            <div className="">
                <h1>Test {params.id}</h1>
            </div>
            <div>
            </div>
        </main>
    );
}
