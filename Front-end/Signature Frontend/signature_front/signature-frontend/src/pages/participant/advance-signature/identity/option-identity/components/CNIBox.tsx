import React, {ReactNode} from 'react';
import {Stack} from '@mui/material';
import {
  NGCorrect,
  NGEYE,
  NGIdentityDelete,
  NGIdentityFaceCard,
} from '@assets/iconExport/Allicon';

import NGText from '@components/ng-text/NGText';
import {Localization} from '@/i18n/lan';
import {FigmaBody} from '@constant/style/themFigma/Body';
import {useTranslation} from 'react-i18next';
import {StyleConstant} from '@constant/style/StyleConstant';
import {FigmaCTA} from '@constant/style/themFigma/CTA';

function CniBox({
  headerCard = (
    <Stack direction={'row'} spacing={'10px'}>
      <NGIdentityFaceCard />
      <NGText text={'Unknown'} sx={{...FigmaBody.BodyMediumBold}} />
    </Stack>
  ),
  btnUpload,
  btnTakePhoto,
  IsSuccess = false,
  nameFile = 'PNG.png',
  btnRemove,
  btnView,
}: {
  IsSuccess?: boolean;
  btnUpload: ReactNode;
  btnTakePhoto: ReactNode;
  nameFile?: string;
  btnRemove?: JSX.Element;
  btnView?: JSX.Element;
  headerCard?: JSX.Element;
}) {
  const {t} = useTranslation();
  return (
    <Stack
      sx={{
        ...StyleConstant.box.cardIdentity,
      }}>
      <Stack
        spacing={'10px'}
        height={IsSuccess ? '93px' : '175px'}
        justifyContent={'space-between'}>
        <Stack direction={'row'} justifyContent={'space-between'}>
          {/** header Card **/}
          {headerCard}
          {/** body Card **/}
          {IsSuccess && (
            <Stack
              direction={'row'}
              alignItems={'center'}
              justifyContent={'center'}
              spacing={'5px'}
              bgcolor={'#197B4A'}
              width={'106px'}
              borderRadius={'8px'}
              gap={'8px'}
              height={'20px'}>
              <NGCorrect
                sx={{width: '12px', height: '8px', color: 'White.main'}}
              />
              <NGText
                text={'Fichier chargé'}
                sx={{fontWeight: 700, fontSize: '8px', color: 'White.main'}}
              />
            </Stack>
          )}
        </Stack>
        {IsSuccess && <NGText text={nameFile} sx={{...FigmaBody.BodySmall}} />}
        {/***condition*/}
        {!IsSuccess && (
          <NGText
            text={t(Localization('identity-page', 'condition-upload'))}
            sx={{...FigmaBody.BodySmall, color: 'Light.main'}}
          />
        )}

        {IsSuccess ? (
          <></>
        ) : (
          <>
            {/** action upload**/}
            {btnUpload}
            {/** action take photo**/}
            {btnTakePhoto}
          </>
        )}
        <Stack
          direction={'row'}
          justifyContent={'space-between'}
          display={IsSuccess ? 'flex' : 'none'}>
          {btnRemove ?? (
            <NGText
              text={'Remplacer le fichier'}
              iconStart={
                <NGIdentityDelete
                  sx={{
                    width: '13px',
                    height: '16px',
                    color: 'primary.main',
                    mr: '5px',
                  }}
                />
              }
              sx={{...FigmaCTA.CtaSmall, color: 'primary.main'}}
            />
          )}
          {btnView ? (
            btnView
          ) : (
            <Stack
              direction={'row'}
              justifyContent={'center'}
              alignItems={'center'}>
              <NGEYE
                sx={{
                  width: '16px',
                  height: '16px',
                  color: 'primary.main',
                }}
              />
              <NGText
                text={'Voir'}
                sx={{
                  ...FigmaCTA.CtaSmall,
                  color: 'Black.main',
                  textAlign: 'right',
                  width: '100%',
                }}
              />
            </Stack>
          )}
        </Stack>
      </Stack>
    </Stack>
  );
}

export default CniBox;
