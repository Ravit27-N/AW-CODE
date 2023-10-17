import {FONT_TYPE} from '@/constant/NGContant';
import {FigmaBody} from '@/constant/style/themFigma/Body';
import {Localization} from '@/i18n/lan';
import {useAppSelector} from '@/redux/config/hooks';
import {Center, HStack, Text, VStack} from '@/theme';
import {pixelToRem} from '@/utils/common/pxToRem';
import {NGInfo, NGRollback} from '@assets/iconExport/ExportIcon';
import {
  TemplateBoxInterface,
  TypeContentInterface,
} from '@components/ng-box-of-model/ngBoxOfModelInterface';
import {BorderBox} from '@components/ng-button/NGButton';
import NGText from '@components/ng-text/NGText';
import AddOutlinedIcon from '@mui/icons-material/AddOutlined';
import {Divider} from '@mui/material';
import Avatar from '@mui/material/Avatar';
import Stack from '@mui/material/Stack';
import {blue, deepOrange, deepPurple, green, red} from '@mui/material/colors';
import {Box} from '@mui/system';
import * as React from 'react';
import {ReactNode} from 'react';
import {useTranslation} from 'react-i18next';
import {useOutletContext} from 'react-router-dom';
import {randomIntArray} from '@/utils/common/random';
export function NGBoxModel() {
  const {t} = useTranslation();
  const {theme} = useAppSelector(state => state.enterprise);
  return (
    <Box
      border={1}
      borderColor={theme[0].mainColor!}
      py={3}
      borderRadius={2}
      bgcolor={'white'}
      height={140}>
      <VStack>
        <Text
          sx={{
            p: 1,
            borderRadius: '25%',
            mb: 1,
            bgcolor: 'Primary.main',
            fontFamily: FONT_TYPE.POPPINS,
          }}>
          <Center>
            <AddOutlinedIcon
              sx={{
                color: 'white',
                fontSize: 20,
              }}
            />
          </Center>
        </Text>
        <NGText
          text={t(Localization('text', 'Nouveau projet'))}
          myStyle={{
            color: 'black.main',
            fontSize: {md: 12, lg: 12},
            fontWeight: 600,
          }}
        />
        <NGText
          text={t(Localization('text', 'à partir de zéro'))}
          myStyle={{
            color: 'black.main',
            fontSize: {md: 12, lg: 12},
            fontWeight: 600,
          }}
        />
      </VStack>
    </Box>
  );
}
export function BoxAddModel({
  title,
  sub1,
  sub2,
  textLabelInBox,
  style,
  noActionOnClickPlus = false,
}: TemplateBoxInterface) {
  const {setTemplatePopup} = useOutletContext<{
    setTemplatePopup: React.Dispatch<React.SetStateAction<boolean>>;
  }>();
  const {t} = useTranslation();

  return (
    <Box
      component="main"
      p={2}
      borderRadius={2}
      height={140}
      sx={{bgcolor: 'white', ...style}}>
      <Stack width={'full'} spacing={0.5}>
        <HStack sx={{justifyContent: 'space-between'}} width={'100%'} mb={1}>
          <BorderBox
            title={textLabelInBox ?? t(Localization('text', 'MODÈLE'))}
            prop={{
              color: '#000000',
              borderRadius: pixelToRem(2),
              gap: pixelToRem(8),
            }}
          />
          {noActionOnClickPlus ? (
            <AddOutlinedIcon sx={{color: 'Primary.main'}} />
          ) : (
            <AddOutlinedIcon
              sx={{color: 'Primary.main'}}
              onClick={() => setTemplatePopup(true)}
            />
          )}
        </HStack>
        <NGText text={title} myStyle={{...FigmaBody.BodyMediumBold}} />
        <NGText text={sub1} myStyle={{...FigmaBody.BodySmall}} />
        <NGText text={sub2} myStyle={{...FigmaBody.BodySmall}} />
      </Stack>
    </Box>
  );
}

const bg = [deepOrange[50], deepPurple[50], green[50], blue[50], red[50]];
const textColor = [
  deepOrange[800],
  deepPurple[800],
  green[800],
  blue[800],
  red[800],
];

export const CircleBox = ({
  text,
  textLabel,
  hasLabel = false,
}: TypeContentInterface) => {
  const genColor = React.useMemo(() => {
    return bg.map(() => randomIntArray(1, 0, bg.length));
  }, []);
  const random = randomIntArray(1, 0, bg.length);
  return (
    <Stack direction={'row'} spacing={1} alignItems={'center'}>
      <Avatar
        sx={{
          bgcolor: bg[genColor[random]],
          fontSize: 24,
          width: '35px',
          height: '35px',
        }}>
        {typeof text === 'string' ? (
          <NGText
            text={text}
            myStyle={{
              color: textColor[genColor[random]],
              fontSize: pixelToRem(11),
              fontWeight: 600,
              lineHeight: pixelToRem(16),
              textTransform: 'uppercase',
            }}
          />
        ) : (
          text
        )}
      </Avatar>
      {hasLabel && textLabel}
    </Stack>
  );
};
export const NGCardCorporate = ({
  title = 'Temps moyen de signature',
  iconTitle = <NGInfo fontSize="small" color="disabled" />,
  semiTitle = '37 minutes',
  iconSemiTitle = (
    <Center
      bgcolor={'greenBright'}
      boxShadow={1}
      p={1}
      borderRadius={'6px'}
      mr={1}>
      <NGRollback fontSize="small" sx={{color: '#197B4A', p: 0}} />
    </Center>
  ),
  borderBottomColorBox = '#197B4A',
}: {
  title: string;
  iconTitle: ReactNode;
  semiTitle: string;
  iconSemiTitle: ReactNode;
  borderBottomColorBox: string;
}) => {
  return (
    <Center
      sx={{
        border: 1,
        borderColor: '#E9E9E9',
        borderBottomWidth: 5,
        borderBottomColor: borderBottomColorBox,
        borderTopWidth: 1,
        borderTopColor: '#E9E9E9',
        borderRadius: '6px',
        height: '100%',
        p: '20px',
        gap: '20px',
      }}>
      <NGText
        text={title}
        myStyle={{
          fontSize: 16,
          fontWeight: 600,
          lineHeight: '28px',
          width: '100%',
        }}
        iconEnd={iconTitle}
      />
      <Divider sx={{width: '95%', height: '2px'}} />
      <NGText
        text={semiTitle}
        myStyle={{
          fontSize: 22,
          fontWeight: 600,
          lineHeight: '32px',
          width: '100%',
        }}
        iconStart={iconSemiTitle}
      />
    </Center>
  );
};
