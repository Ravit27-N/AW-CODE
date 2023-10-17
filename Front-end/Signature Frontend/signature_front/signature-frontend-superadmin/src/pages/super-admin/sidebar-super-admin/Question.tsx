
import {useMediaQuery} from "@mui/material";
import {NGPdfViewMultiplePage} from "@components/ng-pdf-view/NGPdfView";
import aboutUs from '@assets/pdf/AboutUs.pdf';
import Stack from "@mui/material/Stack";

const QuestionCorporate = () => {
  const xl = useMediaQuery(`(max-width:1440px)`);
  const xxl = useMediaQuery(`(max-width:2000px)`);
  let scale = 0;
  const handlerScale = () => {
    if (xl) {
      scale = 2;
    } else if (xxl) {
      scale = 2.5;
    } else {
      scale = 3;
    }
    return scale;
  };
  return (
      <Stack
          width="100%"
          alignItems="center"
          sx={{overflowY: 'hidden'}}
          height={`calc(100vh - ( 55px))`}>
        <NGPdfViewMultiplePage scale={handlerScale()} src={aboutUs}/>
      </Stack>
  );
};
export default QuestionCorporate;
