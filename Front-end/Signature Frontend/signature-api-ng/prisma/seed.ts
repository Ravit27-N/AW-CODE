import {Prisma, PrismaClient} from "@prisma/client";
import {apiTables, MODEL_VERSION} from "../classes/interfaces/DBConstants";
import { $logterm } from "../utils/utils";

const prisma = new PrismaClient();

const data: Prisma.NgConfCreateInput = {
  key: "MODEL_VERSION",
  value: MODEL_VERSION,
};

async function main() {
  $logterm(`START SEEDING`)
  $logterm(`Inserting version ${MODEL_VERSION} in ${apiTables.config} table`) ;
   await prisma.ngConf.create({
    data: data
  });
  $logterm(`SEEDING IS FINISHED`);
}

main()
  .then(async () => {
    await prisma.$disconnect();
  })
  .catch(async (e) => {
    await prisma.$disconnect();
    $logterm(e);
    process.exit(1);
  });
